package nl.stevenbontenbal.chorister.api

import org.springframework.data.mapping.context.PersistentEntities
import org.springframework.data.rest.core.support.SelfLinkProvider
import org.springframework.data.rest.webmvc.mapping.Associations
import org.springframework.data.rest.webmvc.mapping.LinkCollector
import org.springframework.hateoas.Link
import org.springframework.hateoas.Links

// Gratefully borrowed from https://github.com/spring-projects/spring-data-rest/issues/2042#issuecomment-1913914052
class CanonicalLinkCollector(val defaultCollector: LinkCollector) : LinkCollector {
    private val entities = defaultCollector.javaClass.getDeclaredField("entities")
        .apply { isAccessible = true }
        .get(defaultCollector) as PersistentEntities
    private val associationLinks = defaultCollector.javaClass.getDeclaredField("associationLinks")
        .apply { isAccessible = true }
        .get(defaultCollector) as Associations
    private val links = defaultCollector.javaClass.getDeclaredField("links")
        .apply { isAccessible = true }
        .get(defaultCollector) as SelfLinkProvider

    override fun getLinksFor(obj: Any) = this.getLinksFor(obj, Links.NONE)

    override fun getLinksFor(obj: Any, existing: Links): Links {
        val defaultLinks = defaultCollector.getLinksFor(obj, existing)
        var newLinks = defaultLinks.replace(links.createSelfLinkFor(obj).withSelfRel())

        val entity = entities.getRequiredPersistentEntity(obj::class.java)
        entity.doWithAssociations { assoc ->
            try {
                if (!associationLinks.isLinkableAssociation(assoc)) {
                    return@doWithAssociations
                }

                val ownerMetadata = associationLinks.getMetadataFor(assoc.inverse.owner.type)
                if (ownerMetadata == null) {
                    return@doWithAssociations
                }

                val propertyMapping = ownerMetadata.getMappingFor(assoc.inverse)
                val rel = propertyMapping.rel

                // if it is a collection, we can't get the value
                if (assoc.inverse.isCollectionLike ) {
                    return@doWithAssociations
                }

                val fieldValue = obj::class.java.getDeclaredField(assoc.inverse.name)
                    .apply { isAccessible = true }
                    .get(obj) ?: return@doWithAssociations

                newLinks = newLinks.replace(links.createSelfLinkFor(fieldValue).withRel(rel))
            } catch (e: Exception) {
                throw CanonicalLinkCollectionException(e.message, e)
            }
        }

        return newLinks
    }

    override fun getLinksForNested(obj: Any, existing: Links): Links {
        return this.getLinksFor(obj, existing)
    }

    companion object {
        fun Links.replace(link: Link): Links {
            val linkMap = this.associateBy { it.rel }.toMutableMap()
            linkMap[link.rel] = link
            return Links.of(linkMap.values)
        }
    }
}
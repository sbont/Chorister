package nl.stevenbontenbal.cymbali

import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Song(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String,
    var composer: String?,
    var recordingUrl: String?,
    var scoreUrl: String?,
    var addedAt: LocalDateTime = LocalDateTime.now(),
    var slug: String = title.toSlug(),
    @ManyToOne(fetch = FetchType.EAGER)
    var addedBy: User)

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var email: String?,
    var displayName: String?)

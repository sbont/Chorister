package nl.stevenbontenbal.cymbali.configuration

import nl.stevenbontenbal.cymbali.SongRepository
import nl.stevenbontenbal.cymbali.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class CymbaliConfiguration {
    @Bean
    fun databaseInitializer(userRepository: UserRepository,
                            songRepository: SongRepository
    ) = ApplicationRunner {
        /*val smaldini = userRepository.save(
            User(
            email = "smaldini@gmail.com",
            displayName = "Stéphane",
        )
        )
        songRepository.save(
            Song(
            title = "You'll never walk alone",
            composer = "Lee Towers",
            recordingUrl = "https://www.youtube.com/watch?v=OV5_LQArLa0",
            scoreUrl = "https://sheetmusic-free.com/wp-content/uploads/2020/04/Youll-Never-Walk-Alone-Sheet-Music-PDF-Gerry-The-Pacemakers-Youll-Never-Walk-Alone-Piano-Sheet-Music-PDF-Free.png",
            addedBy = smaldini,
        )
        )
        songRepository.save(
            Song(
            title = "Turn Your Eyes Upon Jesus",
            composer = "Unknown",
            recordingUrl = "https://www.youtube.com/watch?v=OV5_LQArLa0",
            scoreUrl = "https://sheetmusic-free.com/wp-content/uploads/2020/04/Youll-Never-Walk-Alone-Sheet-Music-PDF-Gerry-The-Pacemakers-Youll-Never-Walk-Alone-Piano-Sheet-Music-PDF-Free.png",
            addedBy = smaldini,
        )
        )*/
    }


}
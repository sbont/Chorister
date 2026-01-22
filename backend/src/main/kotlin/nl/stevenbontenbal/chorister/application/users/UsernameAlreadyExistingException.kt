package nl.stevenbontenbal.chorister.application.users

import nl.stevenbontenbal.chorister.application.InvalidInputException

class UsernameAlreadyExistingException(message: String) : InvalidInputException(message)
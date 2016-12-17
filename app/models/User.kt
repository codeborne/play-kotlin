package models

import play.db.jpa.Model
import javax.persistence.Entity

@Entity
class User(val name: String) : Model()

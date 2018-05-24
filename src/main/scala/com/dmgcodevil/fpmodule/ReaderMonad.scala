package com.dmgcodevil.fpmodule


// takes context: connection, transaction and etc. and produces a value
// can be used for DI as an alternative to cake pattern
// Read type definition in Scalaz
// type Reader[E, A] = ReaderT[Id, E, A] don't worry about ReaderT,
// source http://blog.originate.com/blog/2013/10/21/reader-monad-for-dependency-injection/
object ReaderMonad {

  case class User(id: String, name: String)

  trait UserRepository {
    def get(id: String): User
  }

  class UserRepositoryImpl extends UserRepository {
    override def get(id: String): User = User(id, "some name")
  }

}

import ReaderMonad._
import com.dmgcodevil.fpmodule.ReaderDI.Application

import scalaz.Reader

object CakePatternDI {


  trait UserService {
    def repo: UserRepository

    def find(id: String): Option[User] = Option(repo.get(id))
  }

  trait UserServiceImpl extends UserService {
    override val repo: UserRepository = new UserRepositoryImpl()
  }

  trait Users {
    self: UserService =>
  }

  class UsersImpl extends Users with UserServiceImpl {}

}

object ReaderDI {


  type Work[T] = Reader[UserRepository, T]

  trait UserService {
    def findUser(id: String): Work[User] = Reader { repo => repo.get(id) }
  }

  class Application(userRepo: UserRepository) extends UserService {
    def run[T](reader: Work[T]): T = {
      reader(userRepo)
    }
  }

}

object ReaderDIApplication extends Application(new UserRepositoryImpl()) {
  def main(args: Array[String]): Unit = {
    val operation = for {
      user1 <- findUser("1")
      user2 <- findUser("2")
    } yield List(user1, user2)
    println(run(operation)) // will print List(User(1,some name), User(2,some name))
  }
}
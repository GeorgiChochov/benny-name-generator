package io.github.georgichochov.core

import io.github.georgichochov.bennycore.BennyName
import io.github.georgichochov.models.{LastName, Title}

import scala.util.Random

object BennyNamePermutator {

  def permutate(
      titles: Set[Title],
      lastNames: Set[LastName]
  ): Set[BennyName] = {
    for {
      lastName <- lastNames
      dynastyNumerals = Random.shuffle((1 to titles.size).toList)
      (title, index) <- titles.zipWithIndex
    } yield {
      BennyName(title.title, lastName.lastName, dynastyNumerals(index))
    }
  }

  def main(args: Array[String]): Unit = {
    permutate(
      Set("Mr.", "Dr.", "Marshall", "Judge").map(t =>
        new Title {
          override def title: String = t
        }
      ),
      Set("Popplewell", "Samson", "Thistlebob", "Rosebottom").map(l =>
        new LastName {
          override def lastName: String = l
        }
      )
    ).map(_.fullName)
      .foreach(println)
  }

}

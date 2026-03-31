package org.apache.james.gatling.utils

import java.util.UUID

import com.github.javafaker.Faker

import scala.util.Random

object RandomStringGenerator {

  val faker: Faker = new Faker()

  val keywords: List[String] = List(
    "keyword0",
    "keyword1",
    "keyword2",
    "keyword3",
    "keyword4",
    "keyword5",
    "keyword6",
    "keyword7",
    "keyword8",
    "keyword9"
  )

  def randomString: String = UUID.randomUUID().toString

  def randomAlphaString(length: Int = 5): String = Random.alphanumeric.take(length).mkString("")

  def randomMeaningWord(): String =
    faker.random().nextInt(0, 9).toInt match {
      case 0 => faker.animal().name()
      case 1 => faker.book().title()
      case 2 => faker.book().author()
      case 3 => faker.country().name()
      case 4 => faker.company().name()
      case 5 => faker.esports().game()
      case 6 => faker.food().vegetable()
      case 7 => faker.food().fruit()
      case 8 => faker.food().spice()
      case 9 => faker.name().name()
    }

  def randomDomain: String = faker.internet().domainName()

  def randomKeyword(): String = {
    val index: Int = faker.random().nextInt(0, 9).toInt
    keywords(index)
  }
}

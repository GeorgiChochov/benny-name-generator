package io.github.georgichochov.bennycore

case class BennyName(
    title: String,
    fancyLastName: String,
    dynasticNumeral: Int
) {
  def fullName: String = s"$title Benedict $fancyLastName $dynasticNumeral"
}

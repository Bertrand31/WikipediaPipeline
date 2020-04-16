package object wikipipeline {

  type WikiStat = (String, Int)

  object WikiStatOrdering extends Ordering[WikiStat] {

    def compare(a: WikiStat, b: WikiStat) = b._2 compare a._2
  }
}

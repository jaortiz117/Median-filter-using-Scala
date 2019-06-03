package filters

trait Filter {
  def filter(inPath: String, outPath:String): Unit
  def name: String
}

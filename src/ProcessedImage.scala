import java.awt.image.BufferedImage

class ProcessedImage(_img: BufferedImage, _dTime: Long, _name: String) {
  def image: BufferedImage = _img
  def timeDifference: Long = _dTime
  def name: String = _name
}

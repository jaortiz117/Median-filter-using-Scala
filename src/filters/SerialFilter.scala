package filters

import java.awt.Color
import java.io.File
import java.util.Arrays

import javax.imageio.ImageIO

object SerialFilter extends Filter {
  def filter(inPath: String, outPath:String): Unit = {

    val pixel = new Array[Color](9)
    val R = new Array[Int](9)
    val B = new Array[Int](9)
    val G = new Array[Int](9)
    val output = new File(outPath)
    val img = ImageIO.read(new File(inPath))

    for (i <- (1 until img.getWidth - 1)) {
      for (j <- (1 until img.getHeight - 1)) {
        pixel(0) = new Color(img.getRGB(i - 1, j - 1))
        pixel(1) = new Color(img.getRGB(i - 1, j))
        pixel(2) = new Color(img.getRGB(i - 1, j + 1))
        pixel(3) = new Color(img.getRGB(i, j + 1))
        pixel(4) = new Color(img.getRGB(i + 1, j + 1))
        pixel(5) = new Color(img.getRGB(i + 1, j))
        pixel(6) = new Color(img.getRGB(i + 1, j - 1))
        pixel(7) = new Color(img.getRGB(i, j - 1))
        pixel(8) = new Color(img.getRGB(i, j))

        for (k <-(0 until 9)) {
          R(k) = pixel(k).getRed
          B(k) = pixel(k).getBlue
          G(k) = pixel(k).getGreen
        }
        Arrays.sort(R)
        Arrays.sort(G)
        Arrays.sort(B)
        img.setRGB(i, j, new Color(R(4), B(4), G(4)).getRGB)
      }
    }
    ImageIO.write(img, "JPG", output)
  }

  def name:String = "serial_"
}

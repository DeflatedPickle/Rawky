@file:Suppress("unused", "SpellCheckingInspection")

package com.deflatedpickle.rawky.api

enum class ImageMetadata(
    val format: String
) {
    BMP("javax_imageio_bmp_1.0"),
    GIF("javax_imageio_gif_image_1.0"),
    JPEG("javax_imageio_jpeg_image_1.0"),
    PNG("javax_imageio_png_1.0"),
    TIFF("javax_imageio_tiff_image_1.0"),
    WBMP("javax_imageio_wbmp_1.0"),
}
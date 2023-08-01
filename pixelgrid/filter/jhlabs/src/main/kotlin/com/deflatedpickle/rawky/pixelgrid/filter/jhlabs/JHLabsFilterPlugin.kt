/* Copyright (c) 2023 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.rawky.pixelgrid.filter.jhlabs

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.rawky.api.FilterCollection
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Blur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.BoxBlur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Bump
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Despeckle
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Gaussian
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Glow
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.HighPass
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.LensBlur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.LightRays
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Maximum
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Median
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Minimum
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.MotionBlur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.OilPainting
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.ReduceNoise
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Sharpen
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.SmartBlur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.Unsharpen
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.blur.VariableBlur
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.AdjustHSB
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.AdjustLevels
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.AdjustRGB
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.ChannelMix
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Contrast
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Curves
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Diffusion
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Dither
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Exposure
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Gain
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Gamma
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Gray
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Grayscale
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.InvertAlpha
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.InvertColour
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Lookup
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.MapColour
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Mask
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Posterize
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Quantize
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Rescale
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Solarize
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Threshold
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.colours.Tritone
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Circle
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Curl
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Diffuse
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Displace
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Dissolve
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.FieldWarp
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Kaleidoscope
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Marble
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Mirror
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Perspective
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Pinch
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Polar
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Shear
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Sphere
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Swim
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Twirl
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Warp
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.distort.Water
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Block
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Border
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Chrome
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Crystallize
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.DottedHalftone
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Emboss
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Feedback
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Light
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.MaskedHalftone
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Noise
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Pointillize
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Shadow
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Shape
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Stamp
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.effects.Weave
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.BrushedMetal
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Caustics
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Cellular
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Checkerboard
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.FourColourGradient
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.FractalBrownianMotion
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Gradient
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.LensFlare
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.PerlinNoise
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Plasma
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Scratch
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Smear
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Sparkle
import com.deflatedpickle.rawky.pixelgrid.filter.jhlabs.texture.Wood

@Plugin(
    value = "jhlabs_filter",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Filters images using the JHLabs filters library
    """,
    type = PluginType.OTHER,
    dependencies =
    [
        "deflatedpickle@pixel_grid#*",
    ],
)
object JHLabsFilterPlugin : FilterCollection() {
    override val name = "JHLabs"
    override val filters = listOf(
        // colour
        ChannelMix,
        Contrast,
        Curves,
        Diffusion,
        Dither,
        Exposure,
        Gain,
        Gamma,
        Gray,
        Grayscale,
        AdjustHSB,
        InvertAlpha,
        InvertColour,
        AdjustLevels,
        Lookup,
        MapColour,
        Mask,
        Posterize,
        Quantize,
        Rescale,
        AdjustRGB,
        Solarize,
        Threshold,
        Tritone,
        // distort
        Circle,
        Curl,
        Diffuse,
        Displace,
        Dissolve,
        FieldWarp,
        Kaleidoscope,
        Marble,
        Mirror,
        Perspective,
        Pinch,
        Polar,
        Shear,
        Sphere,
        Swim,
        Twirl,
        Warp,
        Water,
        // effect
        Block,
        Border,
        Chrome,
        DottedHalftone,
        Crystallize,
        Emboss,
        Feedback,
        MaskedHalftone,
        Light,
        Noise,
        Pointillize,
        Shadow,
        Shape,
        Stamp,
        Weave,
        // texture
        BrushedMetal,
        Caustics,
        Cellular,
        Checkerboard,
        FractalBrownianMotion,
        LensFlare,
        FourColourGradient,
        Gradient,
        Plasma,
        PerlinNoise,
        Scratch,
        Smear,
        Sparkle,
        Wood,
        // blur
        Blur,
        BoxBlur,
        Bump,
        Despeckle,
        Gaussian,
        Glow,
        HighPass,
        LensBlur,
        Maximum,
        Median,
        Minimum,
        MotionBlur,
        OilPainting,
        LightRays,
        ReduceNoise,
        Sharpen,
        SmartBlur,
        Unsharpen,
        VariableBlur,
    )

    init {
        registry[name] = this
        current = this
        default = this
    }
}

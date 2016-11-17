package main

import (
	"github.com/ungerik/go-cairo"

	"math"
)

func main() {
	surface := cairo.NewSurface(cairo.FORMAT_ARGB32, 512, 512)

	da := 2 * math.Pi / math.Pow((math.Sqrt(5)+1)/2, 2)
	a := 0.0
	s := 26.0

	surface.SetSourceRGB(1, 1, 1)
	surface.Arc(256, 256, 210, 0, 2*math.Pi)
	surface.ClosePath()
	surface.Fill()

	surface.SetSourceRGB(float64(0xcc)/255, float64(0xcc)/255, float64(0xcc)/255)
	surface.SetLineWidth(12)
	surface.Arc(256, 256, 207, 0, 2*math.Pi)
	surface.ClosePath()
	surface.Stroke()

	surface.SetSourceRGB(float64(0xec)/255, float64(0x40)/255, float64(0x7a)/255)
	for i := 0; i < 1000; i++ {
		ds := math.Sqrt(float64(i)+.2) * 1.7 * s
		x := 256 + ds*math.Sin(a)
		y := 256 + ds*math.Cos(a)
		a += da
		surface.Arc(x, y, s, 0, 2*math.Pi)
		surface.ClosePath()
		surface.Fill()
		if ds+s > 198 {
			break
		}
	}

	surface.WriteToPNG("phyllo.png")
	surface.Finish()

}

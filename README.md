JSound2Light - A Java based sound to light program that doesn't (touchwood) look as awful as the versions built into lighting fixtures.

This project is still in early development and is currently not functional. I'll likely work on it as and when I have the interest to.

Accepts any device sound input (Dante Virtual Soundcard or similar recommended- not a mic), and outputs it over Artnet to your desired recipient. Also included is a set of rudimentary processing tools, such as a gate, and input smoother to 'clean' the input signal and hopefully improve the quality of the output.




Dependencies:
artnet4j - sending artnet
JTransforms - FFT analysis for generating frequencies from the input

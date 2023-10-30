# Visible Light Communication (VLC) Mobile Application

This repository contains the source code and documentation for a mobile application that explores the potential of Visible Light Communication (VLC) using smartphone cameras. VLC is a rapidly emerging communication method that uses visible light to transmit data. As challenges like the climate crisis and overcrowding of the radio frequency spectrum become more prevalent, alternative and sustainable communication methods like VLC gain importance. This application specifically harnesses Optical Camera Communication (OCC) links, making use of the Rolling Shutter mechanism inherent to most smartphone cameras to decode data.

## Features

- **Data Reception through VLC**: The app is designed to receive data using VLC, offering a glimpse into future communication systems.
- **Optical Camera Communication (OCC)**: This is the heart of the application, allowing data transmission via light and its subsequent reception using a camera.
- **CMOS Image Sensor Integration**: Makes use of the Complementary Metal-Oxide Semiconductor (CMOS) image sensors, a common feature in today's smartphone cameras.
- **Rolling Shutter Decoding**: The unique characteristic of many smartphone cameras, the Rolling Shutter effect, is harnessed for data decoding.

## Results

### Performance Based on Distance

The system's performance was evaluated based on the distance between the transmitter and the receiver. A fixed 16-bit packet was transmitted using a frequency modulation of 2 kHz with On-Off Keying (OOK). Tests were conducted at various distances, using colimating lenses. Results showed variations in the system's processing and decoding capabilities based on the distance between devices.

![Placeholder for Distance-Based Result Image](image-report/image1.png)

### Performance Based on Transmission Frequency

The device's camera captures at a rate of 30 fps (frames per second). The system's performance was also evaluated based on the transmission frequency, particularly at 2, 4, and 6 kHz. It was observed that an increase in transmission frequency does not necessarily result in a higher number of packets recovered per frame.

![Placeholder for Frequency-Based Result Image](image-report/image2.png)

## Conclusion and Future Projects

This research aimed to explore the practicality of using mobile phone cameras in VLC systems by leveraging the Rolling Shutter effect. The project utilized the Google Colaboratory cloud service for signal characterization, observing variations in the signal-to-noise ratio in captured images. Testing the system across different distances and transmission frequencies yielded insights into its capabilities and limitations. While challenges in real-time data recovery persist, the approach shows promise for non-time-critical applications. Future endeavors in this field might benefit from a deeper dive into digital image processing, improved convolution techniques, and the development of more efficient decoding algorithms.

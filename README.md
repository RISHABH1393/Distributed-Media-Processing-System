# Distributed Media Processing System

## Overview
A distributed media processing system that handles file uploads, processes media files, and integrates with Kafka for real-time message handling. Features include format conversion using FFmpeg and local storage for processed files.

## Technologies Used
- **Spring Boot**: For building the microservices.
- **Kafka**: For managing real-time messaging and data flow.
- **FFmpeg**: For media file processing (format conversion, metadata extraction).
- **Local File Storage**: For storing processed media files.

## Setup Instructions
1. **Install Kafka**:
   - Follow the [Kafka installation guide](https://kafka.apache.org/quickstart) to set up Kafka and Zookeeper.
   
2. **Install FFmpeg**:
   - Download and install FFmpeg from [FFmpeg official site](https://ffmpeg.org/download.html).

3. **Clone the Repository**:
   ```bash
   git clone https://github.com/RISHABH1393/Distributed-Media-Processing-System.git
   cd distributed-media-processing-system

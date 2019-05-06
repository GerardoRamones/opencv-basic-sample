OpenCV Basic Sample
=======

## Using Device Camera

This code shows you a very basic android project in _java_ which opens camera through OpenCV (v3.4.5).

_OpenCV Manager_ app is **NOT** required because all required libraries are inside the project.

OpenCV (Open source computer vision) is a library of programming functions mainly 
aimed at real-time computer vision. Originally developed by Intel, it was 
later supported by Willow Garage then Itseez (which was later acquired by Intel). 

The library is cross-platform and free for use under the open-source BSD license. [Wikipedia](https://en.wikipedia.org/wiki/OpenCV)

---

What does this code it do?:

  * Show a standalone implementation (No need to use OpenCV Manager app in the mobile)
  * Shows how to handle, in a basic way, the permissions to use phone camera
  * Shows a view with video stream from camera handled by OpenCV
  
***IMPORTANT***:
If you build a new project, you need to copy the **jniLibs** folder from `app/src/main`.

OpenCV version (included) comes from: [OpenCV Official Site](https://github.com/opencv/opencv/archive/3.4.5.zip)
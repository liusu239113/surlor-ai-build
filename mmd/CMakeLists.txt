cmake_minimum_required(VERSION 3.22.1)
project("mmd_jni_stub")
add_library(MmdWrapper SHARED empty.cpp)
target_link_libraries(MmdWrapper android log)

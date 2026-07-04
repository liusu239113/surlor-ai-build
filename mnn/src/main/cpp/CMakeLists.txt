cmake_minimum_required(VERSION 3.22.1)
project("mnn_jni_stub")
add_library(MnnWrapper SHARED empty.cpp)
target_link_libraries(MnnWrapper android log)

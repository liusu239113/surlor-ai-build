cmake_minimum_required(VERSION 3.22.1)
project("fbx_jni_stub")
add_library(FbxWrapper SHARED empty.cpp)
target_link_libraries(FbxWrapper android log)

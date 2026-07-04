cmake_minimum_required(VERSION 3.22.1)
project("mnn_jni_stub")
add_library(MNN SHARED src/main/cpp/empty.cpp)
target_link_libraries(MNN android log)

add_library(MNNWrapper SHARED src/main/cpp/empty.cpp)
target_link_libraries(MNNWrapper android log MNN)

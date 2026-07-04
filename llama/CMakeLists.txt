cmake_minimum_required(VERSION 3.22.1)
project("llama_jni_stub")
add_library(LlamaWrapper SHARED empty.cpp)
target_link_libraries(LlamaWrapper android log)

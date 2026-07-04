cmake_minimum_required(VERSION 3.22.1)
project("quickjs_jni_stub")
add_library(QuickJsWrapper SHARED empty.cpp)
target_link_libraries(QuickJsWrapper android log)

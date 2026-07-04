cmake_minimum_required(VERSION 3.22.1)

set(LLAMA_CPP_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../third_party/llama.cpp)

add_library(
    LlamaWrapper
    SHARED
    llama_jni_stub.cpp
)

target_compile_definitions(
    LlamaWrapper
    PRIVATE
    OPERIT_HAS_LLAMA_CPP=1
)

target_include_directories(
    LlamaWrapper
    PRIVATE
    ${LLAMA_CPP_DIR}/include
    ${LLAMA_CPP_DIR}/common
    ${LLAMA_CPP_DIR}/vendor
)

target_link_libraries(
    LlamaWrapper
    llama
    llama-common
    android
    log
)

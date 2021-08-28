cmake_minimum_required(VERSION 3.4.1)

include_directories(renderer
        util)

file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/renderer/*.cpp
        ${CMAKE_SOURCE_DIR}/sample/*.cpp)
add_library(
        native_render
        SHARED

        ${src-files}
)


find_library(
        log-lib
        log
)

target_link_libraries(
        native_render

        ${log-lib}
        EGL
        GLESv3
)


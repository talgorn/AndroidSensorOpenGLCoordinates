/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.fix.SensorsAxisViewer;


// A 3D arrow pointing to the sky (Y axis) with standard 3D coloring X=red, Y=Green, Z=Blue
// Build in the standard right handed OpenGL coordinate set.
/*** --> When the device is laid flat pointing north,
// both device and arrow are in default position ***/
public class XyzAsRgb {
    // Define points for x, y and z axis with respective colors r, g, b.
    private float [] geometry = {

        //+X XyzAsRgb, RED
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,

        0.5f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        0.0f, 0.2f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f,//This is the green Y axis

        //-X XyzAsRgb, LIGHT RED
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,

        -0.5f, 0.0f, 0.0f,
        1.0f, 0.6f, 0.6f, 1.0f,

        0.0f, 0.2f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f,//This is the green Y axis

        //+Z XyzAsRgb, BLUE
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,

        0.0f, 0.0f, 0.5f,
        0.0f, 0.0f, 1.0f, 1.0f,

        0.0f, 0.2f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f,//This is the green Y axis

        //-Z XyzAsRgb, LIGHT BLUE
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,

        0.0f, 0.0f, -0.5f,
        0.6f, 0.6f, 1.0f, 1.0f,

        0.0f, 0.2f, 0.0f,
        0.0f, 1.0f, 0.0f, 1.0f};//This is the red Y axis

    public float [] getGeometry() {
        return geometry;
    }
}
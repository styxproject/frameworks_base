/*
 * Copyright (C) 2024 The Android Open Source Project
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
package com.android.internal.widget.remotecompose.core.operations;

import com.android.internal.widget.remotecompose.core.CompanionOperation;
import com.android.internal.widget.remotecompose.core.Operation;
import com.android.internal.widget.remotecompose.core.Operations;
import com.android.internal.widget.remotecompose.core.PaintContext;
import com.android.internal.widget.remotecompose.core.PaintOperation;
import com.android.internal.widget.remotecompose.core.RemoteContext;
import com.android.internal.widget.remotecompose.core.VariableSupport;
import com.android.internal.widget.remotecompose.core.WireBuffer;

import java.util.List;

public class DrawBitmap extends PaintOperation implements VariableSupport {
    public static final Companion COMPANION = new Companion();
    float mLeft;
    float mTop;
    float mRight;
    float mBottom;
    float mOutputLeft;
    float mOutputTop;
    float mOutputRight;
    float mOutputBottom;
    int mId;
    int mDescriptionId = 0;

    public DrawBitmap(
            int imageId,
            float left,
            float top,
            float right,
            float bottom,
            int descriptionId) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        mId = imageId;
        mDescriptionId = descriptionId;
    }

    @Override
    public void updateVariables(RemoteContext context) {
        mOutputLeft = (Float.isNaN(mLeft))
                ? context.getFloat(Utils.idFromNan(mLeft)) : mLeft;
        mOutputTop = (Float.isNaN(mTop))
                ? context.getFloat(Utils.idFromNan(mTop)) : mTop;
        mOutputRight = (Float.isNaN(mRight))
                ? context.getFloat(Utils.idFromNan(mRight)) : mRight;
        mOutputBottom = (Float.isNaN(mBottom))
                ? context.getFloat(Utils.idFromNan(mBottom)) : mBottom;
    }

    @Override
    public void registerListening(RemoteContext context) {
        if (Float.isNaN(mLeft)) {
            context.listensTo(Utils.idFromNan(mLeft), this);
        }
        if (Float.isNaN(mTop)) {
            context.listensTo(Utils.idFromNan(mTop), this);
        }
        if (Float.isNaN(mRight)) {
            context.listensTo(Utils.idFromNan(mRight), this);
        }
        if (Float.isNaN(mBottom)) {
            context.listensTo(Utils.idFromNan(mBottom), this);
        }
    }

    @Override
    public void write(WireBuffer buffer) {
        COMPANION.apply(buffer, mId, mLeft, mTop, mRight, mBottom, mDescriptionId);
    }

    @Override
    public String toString() {
        return "DrawBitmap (desc=" + mDescriptionId + ")" + mLeft + " " + mTop
                + " " + mRight + " " + mBottom + ";";
    }

    public static class Companion implements CompanionOperation {
        private Companion() {
        }

        @Override
        public void read(WireBuffer buffer, List<Operation> operations) {
            int id = buffer.readInt();
            float sLeft = buffer.readFloat();
            float srcTop = buffer.readFloat();
            float srcRight = buffer.readFloat();
            float srcBottom = buffer.readFloat();
            int discriptionId = buffer.readInt();

            DrawBitmap op = new DrawBitmap(id, sLeft, srcTop, srcRight, srcBottom, discriptionId);
            operations.add(op);
        }

        @Override
        public String name() {
            return "DrawOval";
        }

        @Override
        public int id() {
            return Operations.DRAW_BITMAP;
        }

        public void apply(WireBuffer buffer,
                          int id,
                          float left,
                          float top,
                          float right,
                          float bottom,
                          int descriptionId) {
            buffer.start(Operations.DRAW_BITMAP);
            buffer.writeInt(id);
            buffer.writeFloat(left);
            buffer.writeFloat(top);
            buffer.writeFloat(right);
            buffer.writeFloat(bottom);
            buffer.writeInt(descriptionId);
        }
    }

    @Override
    public void paint(PaintContext context) {
        context.drawBitmap(mId, mOutputLeft,
                mOutputTop,
                mOutputRight,
                mOutputBottom);
    }
}

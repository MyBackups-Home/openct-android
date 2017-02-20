package cc.metapro.openct.utils;

/*
 *  Copyright 2016 - 2017 OpenCT open source class table
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.metapro.openct.data.university.item.classinfo.ClassDuring;

public class ClassInfoHelper {

    public static int getLength(String time) {
        int[] tmp = REHelper.getStartEnd(time);
        return tmp[1] - tmp[0] + 1;
    }

    public static String infoParser(int idx, String re, String[] contents) {
        if (idx < contents.length && idx >= 0) {
            String content = contents[idx];
            if (!TextUtils.isEmpty(re)) {
                Matcher matcher = Pattern.compile(re).matcher(content);
                if (matcher.find()) {
                    content = matcher.group();
                }
            }
            return content;
        } else {
            return "";
        }
    }

    public static ClassDuring getClassDuring(String duringRe, String rawDuring) {
        boolean[] weeks = new boolean[30];
        for (int i = 0; i < weeks.length; i++) {
            weeks[i] = false;
        }

        String during = rawDuring;
        if (!TextUtils.isEmpty(duringRe)) {
            Matcher matcher = Pattern.compile(duringRe).matcher(rawDuring);
            if (matcher.find()) {
                during = matcher.group();
            }
        }
        int[] result = REHelper.getStartEnd(during);
        if (result[0] >= 0 && result[0] <= weeks.length && result[1] >= result[0] && result[1] <= weeks.length) {
            for (int i = result[0] - 1; i <= result[1] - 1; i++) {
                weeks[i] = true;
            }
        }
        boolean odd = Pattern.compile("单周?").matcher(rawDuring).find();
        boolean even = Pattern.compile("双周?").matcher(rawDuring).find();
        return new ClassDuring(weeks, odd, even);
    }
}

package cc.metapro.openct.data.university.item.classinfo;

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

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import java.util.Calendar;
import java.util.Set;

import cc.metapro.interactiveweb.utils.HTMLUtils;
import cc.metapro.openct.data.source.StoreHelper;
import cc.metapro.openct.data.university.CmsFactory;
import cc.metapro.openct.utils.ClassInfoHelper;

/**
 * One EnrichedClassInfo Object has one ClassInfo Object
 */
public class EnrichedClassInfo implements Comparable<EnrichedClassInfo> {

    private String name;

    private String type;

    private Set<ClassTime> mTimeSet = new ArraySet<>();

    private int color = Color.parseColor("#968bc34a");

    /**
     * only one class info content should be there
     *
     * @param content string class info content
     * @param weekday day of week
     * @param info    cms class table for class info generation
     * @param color   color of background, same classes share the same color
     */
    public EnrichedClassInfo(String content, int weekday, int dailySeq, int color, CmsFactory.ClassTableInfo info) {
        this(content, weekday, dailySeq, info);
        this.color = color;
    }

    public EnrichedClassInfo(String content, int weekday, int dailySeq, CmsFactory.ClassTableInfo info) {
        String[] tmp = content.split(HTMLUtils.BR_REPLACER);
        name = ClassInfoHelper.infoParser(info.mNameIndex, info.mNameRE, tmp);
        type = ClassInfoHelper.infoParser(info.mTypeIndex, info.mTypeRE, tmp);
        mTimeSet.add(new ClassTime(weekday, dailySeq, tmp, info));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * judge whether this class is on schedule in this week
     *
     * @param week current week or selected week
     * @return time when has class this week
     */
    @NonNull
    Set<ClassTime> hasClassThisWeek(int week) {
        Set<ClassTime> result = new ArraySet<>();
        for (ClassTime time : mTimeSet) {
            Set<ClassDuring> duringSet = time.getDuringSet();
            if (duringSet != null) {
                for (ClassDuring during : duringSet) {
                    if (during.hasClass(week)) {
                        result.add(time);
                    }
                }
            }
        }
        return result;
    }

    /**
     * judge whether this class is on schedule today
     *
     * @param week current week or selected week
     * @return time when has class today
     */
    @NonNull
    Set<ClassTime> hasClassToday(int week) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Set<ClassTime> timeList = hasClassThisWeek(week);
        Set<ClassTime> result = new ArraySet<>();
        for (ClassTime time : timeList) {
            if (time.inSameDay(dayOfWeek)) {
                result.add(time);
            }
        }
        return result;
    }

    /**
     * merge class with same name to one, the differences should be in time, place, teacher, during
     * and time is the key of them
     *
     * @param info another class info which has the same name with this one
     */
    void combine(EnrichedClassInfo info) {
        if (info == null) return;
        mTimeSet.addAll(info.getTimeSet());
    }

    public Set<ClassTime> getTimeSet() {
        return mTimeSet;
    }

    public boolean isEmpty() {
        return mTimeSet == null || mTimeSet.isEmpty();
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : StoreHelper.toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof EnrichedClassInfo) {
            EnrichedClassInfo classInfo = (EnrichedClassInfo) obj;
            return !TextUtils.isEmpty(name) && name.equals(classInfo.name);
        }
        return false;
    }

    @Override
    public int compareTo(@NonNull EnrichedClassInfo o) {
        for (ClassTime time : mTimeSet) {
            for (ClassTime time1 : o.mTimeSet) {
                if (time.inSameDay(time1)) {
                    return time.compareTo(time1);
                }
            }
        }
        return 1;
    }

}

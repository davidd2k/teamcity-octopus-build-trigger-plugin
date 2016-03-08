package com.mjrichardson.teamCity.buildTriggers.ReleaseCreated;

import com.mjrichardson.teamCity.buildTriggers.OctopusDate;

import java.util.Map;

public class Release implements Comparable<Release> {
    public final String id;
    public final OctopusDate assembledDate;
    public final String version;

    public Release(String releaseId, OctopusDate assembledDate, String version) {
        this.id = releaseId;
        this.assembledDate = assembledDate;
        this.version = version;
    }

    @Override
    public String toString() {
        return id + ";" + assembledDate.toString() + ";" + version;
    }

    public static Release Parse(Map item) {
        String id = item.get("Id").toString();
        OctopusDate assembledDate = OctopusDate.Parse(item.get("Assembled").toString());
        String version = item.get("Version").toString();

        return new Release(id, assembledDate, version);
    }

    public static Release Parse(String pair) {
        if (pair == null || pair == "") {
            return new NullRelease();
        }
        final Integer DONT_REMOVE_EMPTY_VALUES = -1;
        final String[] split = pair.split(";", DONT_REMOVE_EMPTY_VALUES);
        final String releaseId = split[0];
        final OctopusDate assembledDate = OctopusDate.Parse(split[1]);
        final String version = split[2];

        Release result = new Release(releaseId, assembledDate, version);
        if (result.equals(new NullRelease()))
            return new NullRelease();
        return result;
    }

    @Override
    public int compareTo(Release o) {
        return o.assembledDate.compareTo(assembledDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != Release.class && obj.getClass() != NullRelease.class)
            return false;
        return toString().equals(obj.toString());
    }
}

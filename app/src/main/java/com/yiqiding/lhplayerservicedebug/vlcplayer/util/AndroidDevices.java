/*****************************************************************************
 * AndroidDevices.java
 *****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.yiqiding.lhplayerservicedebug.vlcplayer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.os.Environment;

public class AndroidDevices {
    public final static String TAG = "VLC/UiTools/AndroidDevices";
    public final static String EXTERNAL_PUBLIC_DIRECTORY = Environment.getExternalStorageDirectory().getPath();

    public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static ArrayList<String> getStorageDirectories() {
        BufferedReader bufReader = null;
        ArrayList<String> list = new ArrayList<String>();
        list.add(EXTERNAL_PUBLIC_DIRECTORY);

        List<String> typeWL = Arrays.asList("vfat", "exfat", "sdcardfs", "fuse", "ntfs", "fat32", "ext3", "ext4",
                "esdfs");
        List<String> typeBL = Arrays.asList("tmpfs");
        String[] mountWL = { "/mnt", "/Removable", "/storage" };
        String[] mountBL = { "/mnt/secure", "/mnt/shell", "/mnt/asec", "/mnt/obb", "/mnt/media_rw/extSdCard",
                "/mnt/media_rw/sdcard", "/storage/emulated" };
        String[] deviceWL = { "/dev/block/vold", "/dev/fuse", "/mnt/media_rw" };

        try {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = bufReader.readLine()) != null) {

                StringTokenizer tokens = new StringTokenizer(line, " ");
                String device = tokens.nextToken();
                String mountpoint = tokens.nextToken();
                String type = tokens.nextToken();

                // skip if already in list or if type/mountpoint is blacklisted
                if (list.contains(mountpoint) || typeBL.contains(type) || Strings.startsWith(mountBL, mountpoint))
                    continue;

                // check that device is in whitelist, and either type or
                // mountpoint is in a whitelist
                if (Strings.startsWith(deviceWL, device)
                        && (typeWL.contains(type) || Strings.startsWith(mountWL, mountpoint))) {
                    int position = Strings.containsName(list, FileUtils.getFileNameFromPath(mountpoint));
                    if (position > -1)
                        list.remove(position);
                    list.add(mountpoint);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            Util.close(bufReader);
        }
        return list;
    }
}

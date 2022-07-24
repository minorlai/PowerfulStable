package com.hzzt.common.entity.resp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: Allen
 * @date: 2022/7/23
 * @description:
 */
@Getter
@Setter
public class ServerResp implements Parcelable {
    private String country;
    private String iconUrl;
    private List<ServerDTO> server;

    @Getter
    @Setter
    public static class ServerDTO implements Parcelable {
        private String serUrl;
        private String key;
        private int weight;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.serUrl);
            dest.writeString(this.key);
            dest.writeInt(this.weight);
        }

        public ServerDTO() {
        }

        protected ServerDTO(Parcel in) {
            this.serUrl = in.readString();
            this.key = in.readString();
            this.weight = in.readInt();
        }

        public static final Creator<ServerDTO> CREATOR = new Creator<ServerDTO>() {
            @Override
            public ServerDTO createFromParcel(Parcel source) {
                return new ServerDTO(source);
            }

            @Override
            public ServerDTO[] newArray(int size) {
                return new ServerDTO[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.country);
        dest.writeString(this.iconUrl);
        dest.writeList(this.server);
    }

    public ServerResp() {
    }

    protected ServerResp(Parcel in) {
        this.country = in.readString();
        this.iconUrl = in.readString();
        this.server = new ArrayList<ServerDTO>();
        in.readList(this.server, ServerDTO.class.getClassLoader());
    }

    public static final Parcelable.Creator<ServerResp> CREATOR = new Parcelable.Creator<ServerResp>() {
        @Override
        public ServerResp createFromParcel(Parcel source) {
            return new ServerResp(source);
        }

        @Override
        public ServerResp[] newArray(int size) {
            return new ServerResp[size];
        }
    };
}

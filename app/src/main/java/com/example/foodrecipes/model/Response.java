
package com.example.foodrecipes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response implements Parcelable {

    @SerializedName("q")
    @Expose
    private String q;
    @SerializedName("from")
    @Expose
    private Integer from;
    @SerializedName("to")
    @Expose
    private Integer to;
    @SerializedName("more")
    @Expose
    private Boolean more;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("hits")
    @Expose
    private List<Hit> hits = null;

    protected Response(Parcel in) {
        q = in.readString();
        if (in.readByte() == 0) {
            from = null;
        } else {
            from = in.readInt();
        }
        if (in.readByte() == 0) {
            to = null;
        } else {
            to = in.readInt();
        }
        byte tmpMore = in.readByte();
        more = tmpMore == 0 ? null : tmpMore == 1;
        if (in.readByte() == 0) {
            count = null;
        } else {
            count = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        if (from == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(from);
        }
        if (to == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(to);
        }
        dest.writeByte((byte) (more == null ? 0 : more ? 1 : 2));
        if (count == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(count);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Boolean getMore() {
        return more;
    }

    public void setMore(Boolean more) {
        this.more = more;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

}

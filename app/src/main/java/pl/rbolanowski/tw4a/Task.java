package pl.rbolanowski.tw4a;

import android.os.Parcelable;
import android.os.Parcel;

public class Task implements Parcelable {

    public String uuid;
    public String description;
    public boolean done;

    public Task() {}

    public static final Parcelable.Creator<Task> CREATOR
             = new Parcelable.Creator<Task>() {
         
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(uuid);
        out.writeString(description);
        boolean[] booleanArray = { done };
        out.writeBooleanArray(booleanArray);
    }

    private Task(Parcel in) {
        uuid = in.readString();
        description = in.readString();
        boolean[] booleanArray = new boolean[1];
        in.readBooleanArray(booleanArray);
        done = booleanArray[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }
}


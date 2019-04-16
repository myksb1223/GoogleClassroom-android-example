package com.example.grexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;

import java.util.List;

/**
 * Created by seungbeomkim on 2019. 4. 16..
 */

public class ThirdPartyCoursesActivity extends ThirdPartyLoginActivity {
    private static final String TAG = "TPCActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getBtn = findViewById(R.id.getBtn);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get courses.
                mClassroomServiceHelper.listCourses()
                        .addOnSuccessListener(new OnSuccessListener<List<Course>>() {
                            @Override
                            public void onSuccess(List<Course> courses) {
                                if(courses.size() > 0) {
                                    Course course = courses.get(0);

                                    // Get courseworks in selected course.
                                    mClassroomServiceHelper.listCourseWorks(course.getId())
                                            .addOnSuccessListener(new OnSuccessListener<List<CourseWork>>() {
                                                @Override
                                                public void onSuccess(List<CourseWork> courseWorks) {
                                                    Log.d(TAG, "CourseWork size : " + courseWorks.size());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }

    @Override
    protected void afterLogout() {

    }

    @Override
    protected void updateUI(boolean login) {

    }
}

package com.example.grexample;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.ListCourseWorkResponse;
import com.google.api.services.classroom.model.ListCoursesResponse;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by seungbeomkim on 2019. 4. 16..
 */

public class ClassroomServiceHelper {

    // If you don't want to multiThread, use 'newSingleThreadExecutor' method.
    private final Executor mExecutor = Executors.newFixedThreadPool(5);
    private final Classroom mClassroomService;

    public ClassroomServiceHelper(Classroom ClassroomService) {
        mClassroomService = ClassroomService;
    }

    public Task<List<Course>> listCourses() {
        return Tasks.call(mExecutor, new Callable<List<Course>>() {
            @Override
            public List<Course> call() throws Exception {
                ListCoursesResponse response = mClassroomService.courses().list()
                        .setPageSize(10)
                        .execute();

                List<Course> courses = response.getCourses();

                return courses;
            }
        });
    }

    public Task<List<CourseWork>> listCourseWorks(final String courseId) {
        return Tasks.call(mExecutor, new Callable<List<CourseWork>>() {
            @Override
            public List<CourseWork> call() throws Exception {
                ListCourseWorkResponse response = mClassroomService.courses().
                        courseWork().list(courseId).execute();


                List<CourseWork> courseWorks = response.getCourseWork();

                return courseWorks;
            }
        });
    }

    // You can make other method for getting assignments so on.
}

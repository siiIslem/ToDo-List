package com.example;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Task implements Serializable {
    private String title;
    private boolean completed;
    private boolean priority; // Indicates if the task is a priority
    private final Date creationDate; // Stores the task creation date

    // Constructor
    public Task(String title) {
        this.title = title;
        this.completed = false;
        this.priority = false; // Default: not a priority
        this.creationDate = new Date(); // Set creation date to the current date and time
    }

    // Getter for creation date
    public Date getCreationDate() {
        return creationDate;
    }

    // Getter for formatted creation date
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.ENGLISH);        
        return formatter.format(creationDate);
    }

    // Getter and Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for completed
    public boolean isCompleted() {
        return completed;
    }

    public void toggleCompletion() {
        this.completed = !this.completed;
    }

    // Getter and Setter for priority
    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    // Override equals to compare tasks based on title
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return title.equals(task.title);
    }

    //Added it just to remove the "generate missing hashcode warning" in equals
    @Override
    public int hashCode() {
        return Objects.hash(title); // Using Objects.hash() to generate the hash code based on the title
    }


    // Override toString to display the task title
    @Override
    public String toString() {
        return title;
    }
}

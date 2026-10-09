package com.course.dbsecurity.note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/*
 * Note - a private note that belongs to ONE user (the owner).
 * We keep only the owner's username to keep the example small
 * (a real app would use @ManyToOne AppUser owner - see topic 40).
 */
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 2000)
    private String content;

    @Column(nullable = false, length = 50)
    private String ownerUsername;

    protected Note() { }

    public Note(String title, String content, String ownerUsername) {
        this.title = title;
        this.content = content;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getOwnerUsername() { return ownerUsername; }
}

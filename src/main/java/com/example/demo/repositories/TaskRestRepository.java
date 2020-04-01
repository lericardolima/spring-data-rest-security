package com.example.demo.repositories;

import com.example.demo.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tasks", collectionResourceRel = "tasks")
public interface TaskRestRepository  extends JpaRepository<Task, Long> {

}

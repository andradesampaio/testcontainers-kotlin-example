package com.maschnetwork.testcontainers.api

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class Team(val name: String)

@RestController
class TeamController(private val teamService: TeamService) {

    @GetMapping("/api/teams/{name}")
    fun getTeams(@PathVariable name : String) : ResponseEntity<Team> {
        return ResponseEntity.ok(teamService.getTeam(name) ?: throw NotFoundException("name"))
    }

    @PostMapping("/api/teams")
    fun createTeam(@RequestBody body: Map<String, String>) : ResponseEntity<Team> {
        val createdTeam = teamService.createTeam(body["name"] ?: throw IllegalArgumentException("Name not provided"))
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam)
    }

}

@Service
class TeamService {

    @Cacheable(cacheNames = ["team"], key = "#name")
    fun getTeam(name : String) : Team? {
        return null
    }

    @CachePut(cacheNames = ["team"], key = "#name")
    fun createTeam(name : String) : Team  {
        return Team(name = name)
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException (typeName : String) : RuntimeException("$typeName not found")
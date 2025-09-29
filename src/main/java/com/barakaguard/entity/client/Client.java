package main.java.com.barakaguard.entity.client;

import java.util.UUID;

public record Client(UUID id, String nom, String email) {
}

package org.example.transferencia.web;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transferencia")
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class TransferenciaController {

}
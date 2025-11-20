package be.ucll.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.model.Process;
import be.ucll.service.ProcessService;

@RestController
@RequestMapping("/process")
@CrossOrigin(origins = "*")
public class ProcessController {
  
  @Autowired
  private ProcessService processService;

  @GetMapping
  public List<Process> getProcesses(){
    return processService.getProcesses();  
  }
}

package be.ucll.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.ucll.model.Process;
import be.ucll.repository.ProcessRepository;

@Service
public class ProcessService {

  @Autowired
  private ProcessRepository processRepository; 

  public List<Process> getProcesses(){
    return processRepository.findAll();
  }
}

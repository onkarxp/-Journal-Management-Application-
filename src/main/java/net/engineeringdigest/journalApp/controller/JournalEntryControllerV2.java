package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")  //this adds a mapping to the whole class below
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;  //service injection in controller

    @Autowired
    private UserService userService;

    //  1. GET THE DATA (ENTRIES): FIND ALL     **with resonseEntity
    @GetMapping("/{userName}")          //localhost:8080/journal GET
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        List<JournalEntry> all = user.getJournalEntries();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    //  2. POST THE DATA (ENTRIES): CREATE ENTRY  **with responseEntity
    @PostMapping("/{userName}")        //localhost:8080/journal PUT
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName) {
        try {
            myEntry.setDate(LocalDateTime.now());              //automatically sets the date
            journalEntryService.saveEntry(myEntry, userName);            //called saveEntry Method from service
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    //  3.GET THE DATA (ENTRIES) BY ID: **with responseEntity
    @GetMapping("id/{myID}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myID) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myID);
        //this is used because return type is optional in service
        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // 4. DELETE THE DATA (ENTRIES) BY ID: **with responseEntity
    @DeleteMapping("id/{userName}/{myID}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myID, @PathVariable String userName) {   // ? -> question mark is known as wild card pattern
        journalEntryService.deleteById(myID, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // 5. UPDATE THE DATA (ENTRIES):  **with resonseEntity
    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable String id,
                                                    @RequestBody JournalEntry newEntry,
                                                    @PathVariable String userName
    ) {
        JournalEntry old = journalEntryService.findById(new ObjectId(id)).orElse(null);

        if (old != null) {
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());
            journalEntryService.saveEntry(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

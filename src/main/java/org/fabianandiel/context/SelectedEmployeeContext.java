package org.fabianandiel.context;

import org.fabianandiel.entities.Person;

public class SelectedEmployeeContext {

        private static Person personToUpdate=null;

        private SelectedEmployeeContext() {
        }

        public static Person getPersonToUpdate() {
            return personToUpdate;
        }

        public static void initSession(Person person) {
            personToUpdate = person;
        }


        public static void clearSession() {
            personToUpdate = null;
        }

    }



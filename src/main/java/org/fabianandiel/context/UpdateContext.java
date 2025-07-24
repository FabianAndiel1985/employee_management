package org.fabianandiel.context;

import org.fabianandiel.entities.Person;

public class UpdateContext {

        private static Person personToUpdate=null;

        private UpdateContext() {
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



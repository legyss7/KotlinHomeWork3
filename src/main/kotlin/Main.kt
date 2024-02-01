package org.example

/**
 * Продолжаем дорабатывать домашнее задание из предыдущего семинара.
 * За основу берём код решения из предыдущего домашнего задания.
 *
 * — Измените класс Person так, чтобы он содержал список телефонов и
 * список почтовых адресов, связанных с человеком.
 * — Теперь в телефонной книге могут храниться записи о нескольких людях.
 * Используйте для этого наиболее подходящую структуру данных.
 * — Команда AddPhone теперь должна добавлять новый телефон к записи
 * соответствующего человека.
 * — Команда AddEmail теперь должна добавлять новый email к записи
 * соответствующего человека.
 * — Команда show должна принимать в качестве аргумента имя человека
 * и выводить связанные с ним телефоны и адреса электронной почты.
 * — Добавьте команду find, которая принимает email или телефон
 * и выводит список людей, для которых записано такое значение.
 */
fun main() {
    val people: MutableList<Person> = mutableListOf()
    while (true) {
        when (val command = readCommand()) {

            is CheckAddCommand -> {
                if (command.isValid()) {
                    val person = people.find { it.name == command.name }
                    if (person == null) {
                        val newPerson = Person(command.name, mutableListOf(), mutableListOf())
                        if (command.type == "addPhone") {
                            newPerson.phones.add(command.value)
                        } else {
                            newPerson.emails.add(command.value)
                        }
                        people.add(newPerson)
                    } else {
                        if (command.type == "addPhone") {
                            person.phones.add(command.value)
                        } else {
                            person.emails.add(command.value)
                        }
                    }
                } else {
                    println("Неправильно введены данные.")
                    println(HelpCommand.info())
                }
            }

            is ShowCommand -> {
                val person = people.find { it.name == command.name }
                if (person != null) {
                    println("Последний добавленный контакт: $person")
                } else {
                    println("Контакт не найден.")
                }
            }

            is FindCommand -> {
                val result = people.filter {
                    if (command.type == "email") {
                        it.emails.contains(command.value)
                    } else {
                        it.phones.contains(command.value)
                    }
                }
                if (result.isNotEmpty()) {
                    println("Найденные контакты:")
                    result.forEach { println(it) }
                } else {
                    println("Контакты не найдены.")
                }
            }

            HelpCommand -> println(HelpCommand.info())

            ExitCommand -> {
                println(ExitCommand.info())
                break
            }

            InputError -> {
                println(InputError.info())
                println(HelpCommand.info())
            }

        }
    }
}

sealed interface Command {
    fun isValid(): Boolean
}


data object InputError : Command {
    fun info(): String {
        return "Некорректная команда. Выводим help для получения справки."
    }

    override fun isValid(): Boolean = true
}


data class ShowCommand(val name: String) : Command {
    fun info(): Any {
        return "Not implemented"
    }

    override fun isValid(): Boolean = true
}

data object HelpCommand : Command {
    fun info(): String {
        return "Список доступных команд:\n" +
                "addPhone <Имя> <Номер телефона> - добавить номер телефона для контакта\n" +
                "addEmail <Имя> <Адрес электронной почты> - добавить адрес электронной почты для контакта\n" +
                "show <Имя> - вывести последний добавленный контакт\n" +
                "findEmail <Адрес электронной почты> - найти контакты по адресу электронной почты\n" +
                "findPhone <Номер телефона> - найти контакты по номеру телефона\n" +
                "help - вывести справку\n" +
                "exit - выход из программы\n"
    }

    override fun isValid(): Boolean {
        return true
    }
}

data object ExitCommand : Command {
    fun info(): String {
        return "Выход из программы\n"
    }

    override fun isValid(): Boolean = true
}

data class FindCommand(val type: String, val value: String) : Command {
    override fun isValid(): Boolean {
        return true
    }
}

data class Person(val name: String, val phones: MutableList<String>, val emails: MutableList<String>) {
    override fun toString(): String {
        return "Имя: $name\nТелефоны:$phones\nEmail: $emails"
    }
}

fun readCommand(): Command {
    val type: String
    val name: String
    val value: String

    print("Введите команду: ")
    val command = readlnOrNull()?.split(" ")
    type = command!![0]

    return when (type) {
        "addPhone" -> {
            name = command[1]
            value = command[2]
            CheckAddCommand(type, name, value)
        }

        "addEmail" -> {
            name = command[1]
            value = command[2]
            CheckAddCommand(type, name, value)
        }

        "show" -> {
            name = command[1]
            ShowCommand(name)
        }

        "findEmail" -> {
            value = command[1]
            FindCommand("email", value)
        }

        "findPhone" -> {
            value = command[1]
            FindCommand("phone", value)
        }

        "help" -> HelpCommand
        "exit" -> ExitCommand

        else -> {
            InputError
        }
    }
}

class CheckAddCommand(val type: String, val name: String, val value: String) : Command {
    override fun isValid(): Boolean {
        val phonePattern = """\+\d{12}$"""
        val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
        val namePattern = """[a-zA-Z]{2,}$"""
        if (type == "addPhone") {
            return when {
                name.matches(Regex(namePattern)) && value.matches(Regex(phonePattern)) -> {
                    true
                }

                else -> false
            }
        } else {
            return when {
                name.matches(Regex(namePattern)) && value.matches(Regex(emailPattern)) -> {
                    true
                }

                else -> false
            }
        }
    }
}

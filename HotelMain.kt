package hotel.lv2

import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// 숫자 외 입력시 오류처리
fun String.isNumeric(): Boolean {
    return try {
        this.toInt()
        true
    } catch (e: Exception) {
        false
    }
}

// 이게 체크인 체크아웃 날짜 담는건가??
fun withInDate(stx: LocalDate, etx: LocalDate, check: LocalDate): Boolean {
    return !check.isBefore(stx) && check.isBefore(etx)
}

//@Suppress("UNREACHABLE_CODE")
fun main() {
    val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val peoples = arrayListOf<People>()
    val rsvHistory = arrayListOf<RsvHistory>()

    while (true) {
        println("호텔예약 프로그램 입니다.")
        println("[메뉴]")
        println("1. 방예약, 2. 예약목록 출력, 3. 예약목록(정렬) 출력, 4. 시스템 종료, 5. 금액 입금-출금 내역 목록 출력, 6. 예약 변경/취소")
        var infoMenu = readln()  // 메뉴 선택
        if (!infoMenu.isNumeric()) {
                System.err.println("메뉴 입력은 숫자만 해주세요.")
                continue
            }

        when (infoMenu.toInt()) {
                1 -> {
                    println("${infoMenu}번을 선택하셨습니다")
                    println("호텔 예약을 시작합니다.")

                    val randomMoney = (500000..1000000).random() // 랜덤 고객 잔액 설정

                    println("예약자 성함을 입력해주세요.")
                    val name = readln() // 예약자 이름 입력
                    var roomNum: Int // 방 번호

                    // 방 번호 지정
                    while (true) {
                        println("예약하실 방 번호를 입력해주세요. (100 ~ 999)")
                        var emptyRoom = readln() // 방 예약번호 입력

                        if (!emptyRoom.isNumeric()) {
                            System.err.println("방 번호 입력은 숫자만 해주세요.")
                            continue
                        }

                        if (emptyRoom.toInt() in 100..999) {
                            roomNum = emptyRoom.toInt() // 범위 맞게 입력시 Int로 바꿔서 저장
                            println("선택하신 방 번호는 ${roomNum} 입니다.")
                            break
                        } else {
                            System.err.println("예약 가능한 방 번호가 아닙니다. 다시 입력해주세요. (100 ~ 999)")
                            continue
                        }
                    }

                    // 여기서 현재날짜 불러오고
                    var checkIn: LocalDate? = null
                    while (true) {
                        println("오늘 날짜는 ${LocalDate.now()} 입니다. 체크인 날짜를 입력해주세요.(yyyyMMdd)")
                        val checkDate = readln() // 체크인 날짜 입력

                        try {
                            val temp = LocalDate.from(dateFormat.parse(checkDate))
                            if (temp.isBefore(LocalDate.now())) {
                                System.err.println("예약이 불가능합니다.")
                                println("오늘 : ${LocalDate.now()} 또는 이후 날짜를 입력해주세요.")
                                continue
                            }
                            val existRoom = rsvHistory.filter { it.resvRoom == roomNum }
                            if (existRoom.isNotEmpty()) {
                                for (emptyRoom in existRoom) {
                                    if (!withInDate(emptyRoom.checkIn, emptyRoom.checkOut, temp)) {
                                        checkIn = temp
                                    } else {
                                        System.err.println("해당 날짜는 이미 예약된 방입니다.")
                                        println("다른 방을 선택해주세요.")
                                        break
                                    }
                                }

                            } else {
                                checkIn = temp
                            }

                            if (checkIn == null) {
                                continue
                            } else {
                                break
                            }

                        } catch (e: DateTimeException) {
                            System.err.println("올바르지 않은 입력입니다.")
                            println("날짜 형식을 주의하여 다시 입력해 주세요. (yyyyMMdd)")
                            continue
                        }
                    }

                    // 체크아웃 날짜 지정
                    var checkOut: LocalDate? = null
                    while (true) {
                        println("예약 날짜는 ${checkIn} 입니다.")
                        println("체크아웃 날짜를 입력해 주세요. (yyyyMMdd)")
                        var checkDate = readln() // 체크아웃 날짜 입력

                        try {
                            val temp = LocalDate.from(dateFormat.parse(checkDate))
                            if (temp.isBefore(checkIn) || temp.isEqual(checkIn)) {
                                System.err.println("체크인 날짜와 같거나 이전입니다. 다시 입력해 주세요.")
                                continue
                            }

                            val existRoom = rsvHistory.filter { it.resvRoom == roomNum }
                            if (existRoom.isNotEmpty()) {
                                for (emptyRoom in existRoom) {
                                    if (!withInDate(emptyRoom.checkIn, emptyRoom.checkOut, temp)) {
                                        checkOut = temp
                                    } else {
                                        println("해당 날짜는 방을 사용중입니다. 다른 날짜를 입력해주세요.")
                                        break
                                    }
                                }
                            } else {
                                checkOut = temp
                            }

                            if (checkOut == null) {
                                continue
                            }
                            break

                        } catch (e: DateTimeParseException) {
                            System.err.println("올바르지 않은 입력입니다.")
                            println("날짜 형식을 주의하여 다시 입력해 주세요. (yyyyMMdd)")
                            continue
                        }
                    }

                    // 명단 추가
                    var people = peoples.find { it.name == name }
                    if (people == null) {
                        people = People(name = name)
                        peoples.add(people)
                    }

                    if (people.money.outBalance(randomMoney, "reserve")) {
                        RsvHistory(
                            people = people,
                            resvMoney = randomMoney,
                            checkIn = checkIn!!,
                            checkOut = checkOut!!,
                            resvRoom = roomNum
                        ).run {
                            rsvHistory.add(this)
                        }

                        println("호텔 예약이 완료되었습니다. 감사합니다.")

                    } else {
                        System.err.println("통장 잔액이 부족합니다. 예약에 실패했습니다.")

                    }
                }

                2 -> {
                    println("========================== 호텔 예약자 명단입니다.==========================")
                    for (i in 0 until rsvHistory.size) {
                        val item = rsvHistory[i]
                        println("${i + 1}. 예약자명: ${item.people.name} | 방번호: ${item.resvRoom} | 체크인: ${item.checkIn} | 체크아웃: ${item.checkOut} ")
                        println("========================================================================")
                    }
                    println("프로그램을 종료하시려면 1번, 메뉴로 돌아가시려면 아무 키나 눌러주세요.")
                    val keyNum = readln()
                    when (keyNum) {
                        "1" -> {
                            println("프로그램을 종료합니다.")
                            return
                        }
                        else -> {
                            return main()
                        }
                    }
                }


                3 -> {
                    println("========================== 호텔 예약자 명단입니다.==========================")
                    val sortedArray = rsvHistory.sortedBy { item -> item.people.name }
                    for (i in sortedArray.indices) {
                        val item = sortedArray[i]
                        println("${i + 1}. 예약자명: ${item.people.name} | 방번호: ${item.resvRoom} | 체크인: ${item.checkIn} | 체크아웃: ${item.checkOut} ")
                        println("========================================================================")
                    }
                    println("프로그램을 종료하시려면 1번, 메뉴로 돌아가시려면 아무 키나 눌러주세요.")
                    val keyNum = readln()
                    when (keyNum) {
                        "1" -> {
                            println("프로그램을 종료합니다.")
                            return
                        }
                        else -> {
                            return main()
                        }
                    }
                }

                4 -> {
                    System.err.println("호텔 예약을 종료합니다.")
                    break
                }

                5 -> {
                    println("금액 입금 - 출금 내역입니다. 조회하실 사용자 이름을 입력하세요.")
                    val name = readln()
                    with(peoples.find { it.name == name }) {
                        if (this == null) {
                            println("입력하신 사용자 이름을 내역에서 찾을 수 없습니다.")
                        } else {
                            this.money.printHistory()
                        }
                    }
                }

                6 -> {
                    println("예약 변경 취소")
                }

                else -> {
                    println("올바른 메뉴 번호를 입력하세요.")
                }
            }
    }
}
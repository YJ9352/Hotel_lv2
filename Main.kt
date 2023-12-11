package hotel.lv2

import com.example.hotelreservation.isNumeric

// 숫자 외 입력시 오류처리
fun String.isNumeric(): Boolean {
    return try {
        this.toInt()
        true
    } catch (e: Exception) {
        false
    }
}

fun main() {
    println("호텔예약 프로그램 입니다.")
    println("[메뉴]")
    println("1. 방예약, 2. 예약목록 출력, 3. 예약목록(정렬) 출력, 4. 시스템 종료, 5. 금액 입금-출금 내역 목록 출력, 6. 예약 변경/취소")

    while (true) {
        var infoMenu = readLine()  // 메뉴 선택

        if (infoMenu != null) {
            if (!infoMenu.isNumeric()) {
                System.err.println("메뉴 입력은 숫자만 해주세요.")
                continue
            } else {
                val hotelmain = HotelMain()
                hotelmain.hotelMenu(infoMenu.toInt()) // 아래 Int 로 지정하고 짜버려서 변환
            }
        }
    }
}

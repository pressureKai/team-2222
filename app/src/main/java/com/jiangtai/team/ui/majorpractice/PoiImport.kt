package com.jiangtai.team.ui.majorpractice

import com.blankj.utilcode.util.LogUtils
import com.jiangtai.team.bean.Person
import com.jiangtai.team.bean.Project
import com.jiangtai.team.bean.TaskBean
import com.jiangtai.team.util.CommonUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

object PoiImport {
    @Throws(IOException::class)
    fun ExcelReader(file: File): TaskBean {

        LogUtils.e("current Thread name is ${Thread.currentThread().name}")
        //实体化一个任务实体类
        val task = TaskBean()
        var workbook: Workbook? = null
        var inputStream: FileInputStream? = null
        val fileName = file.name
        val fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
        inputStream = FileInputStream(file)
        if (fileType.equals("xls", ignoreCase = true)) {
            workbook = HSSFWorkbook(inputStream)
        } else if (fileType.equals("xlsx", ignoreCase = true)) {
            workbook = XSSFWorkbook(inputStream)
        }

        //人员列表
        val resultDataList: ArrayList<Person> = ArrayList<Person>()

        //逐行解析
        for (sheetNum in 0 until workbook!!.numberOfSheets) {
            //返回一个行列表
            val sheet = workbook.getSheetAt(sheetNum)
            if (sheet != null) {
                //获取第一行下标
                val firstRowNum = sheet.firstRowNum
                //第二行
                val secondRow = sheet.getRow(firstRowNum + 1)

                //获取第一行
                val firstRow = sheet.getRow(firstRowNum)
                //获取taskId  (firstCellNum 第一个空格下标)
                task.taskId = firstRow.getCell(firstRow.firstCellNum + 1).toString()
                //获取任务所对应的考核项目ID列表
                val projectIds = firstRow.getCell(firstRow.firstCellNum + 3).toString()


                try {
                    //获取计划开始时间
                    val projectStartTime = firstRow.getCell(firstRow.firstCellNum + 5).toString()

                    task.startTime =  CommonUtil.stringToLong(
                        projectStartTime,
                        "yyyy.MM.dd HH:mm"
                    )
                }catch (e:Exception){
                    LogUtils.e("PoiImport parse time error is $e")
                }


                try {
                    //获取计划结束时间
                    val projectEndTime = firstRow.getCell(firstRow.firstCellNum + 7).toString()
                    task.endTime =  CommonUtil.stringToLong(
                        projectEndTime,
                        "yyyy.MM.dd HH:mm"
                    )
                }catch (e:java.lang.Exception){
                    LogUtils.e("PoiImport parse time error is $e")
                }


                //考核项目ID(以逗号分隔将项目ID分隔成一个String列表)
                val projectId = projectIds.split(",").toTypedArray()

                //任务的任务名称
                task.taskName = secondRow.getCell(secondRow.firstCellNum + 1).toString()
                //获取任务所对应的考核项目名称列表
                val projectNames = secondRow.getCell(secondRow.firstCellNum + 3).toString()

                //是否开启位置定位
                val isLocation = secondRow.getCell(secondRow.firstCellNum + 7).toString()

                //考核项目名称(以逗号分隔将项目ID分隔成一个String列表)
                val projectName = projectNames.split(",").toTypedArray()
                //任务所对应的项目实体列表
                val projects: ArrayList<Project> = ArrayList()


                //项目实体类实例化所需下标
                var projectStart: Int = 0
                //实体化项目列表
                while (projectStart < projectId.size) {
                    val project = Project()
                    //将ID字母大写化并赋值
                    project.projectId = projectId[projectStart].toUpperCase(Locale.ROOT)
                    //项目名称
                    project.projectName = projectName[projectStart]


                    //对应任务的位置
                    val currentProjectIndex = projectStart + 3


                    try {
                        //开始时间
                        project.startTime = CommonUtil.stringToLong(
                            sheet.getRow(currentProjectIndex).getCell(3).toString(),
                            "yyyy.MM.dd HH:mm"
                        )
                    } catch (e: Exception) {
                        LogUtils.e("PoiImport parse startTime error is $e")
                    }


                    try {
                        //结束时间
                        project.endTime = CommonUtil.stringToLong(
                            sheet.getRow(currentProjectIndex).getCell(4).toString(),
                            "yyyy.MM.dd HH:mm"
                        )
                    } catch (e: java.lang.Exception) {
                        LogUtils.e("PoiImport parse endTime error is $e")
                    }

                    //项目内容
                    project.projectContent = sheet.getRow(currentProjectIndex).getCell(2).toString()

                    //项目人员ID (获取人员列表字符串)
                    project.peopleId = sheet.getRow(currentProjectIndex).getCell(5).toString()

                    projects.add(project)
                    ++projectStart
                }

                //任务项目列表赋值
                task.projects = projects
                task.isLocation = isLocation != "0"
                //人员列表开始的下标
                projectStart = firstRowNum + 3 + (projects.size + 1)
                //最后一行下标
                val rowEnd = sheet.physicalNumberOfRows
                //for循环解析人员列表
                for (rowNum in projectStart until rowEnd) {
                    val row = sheet.getRow(rowNum)
                    if (null != row) {
                        val cellNum = 0
                        val person = Person()
                        var var25 = cellNum + 1
                        person.name = row.getCell(cellNum).toString()
                        person.personId = row.getCell(var25++).toString().toUpperCase(Locale.ROOT)
                        person.watchId = row.getCell(var25++).toString()
                        person.sex = row.getCell(var25++).toString()
                        person.gradeId = row.getCell(var25++).toString()
                        person.gradeName = row.getCell(var25++).toString()
                        try {
                            person.deviceListLocal =  row.getCell(var25++).toString()
                            val takeDevice = row.getCell(var25++).toString()
                            LogUtils.e("PoiImport","takeDevice is $takeDevice")
                            person.takeDevice = takeDevice
                        }catch (e:Exception){
                            LogUtils.e("PoiImport","error is $e")
                        }
                        resultDataList.add(person)
                    }
                }
                task.persons = resultDataList
            }
        }
        return task
    }


    /**
    * @des 判断是否是专项计划表格
    * @time 2021/8/9 6:46 下午
    */
    fun isMajorType(file: File) : Boolean{
        var isMajor = false
        var workbook: Workbook? = null
        var inputStream: FileInputStream? = null
        try {
            val fileName = file.name
            val fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
            inputStream = FileInputStream(file)
            if (fileType.equals("xls", ignoreCase = true)) {
                workbook = HSSFWorkbook(inputStream)
            } else if (fileType.equals("xlsx", ignoreCase = true)) {
                workbook = XSSFWorkbook(inputStream)
            }

            for (sheetNum in 0 until workbook!!.numberOfSheets) {
                //返回一个行列表
                val sheet = workbook.getSheetAt(sheetNum)
                if (sheet != null) {
                    //获取第一行下标
                    val firstRowNum = sheet.firstRowNum
                    //第二行
                    val secondRow = sheet.getRow(firstRowNum + 1)
                    //获取计划类别
                    val projectType = secondRow.getCell(secondRow.firstCellNum + 5).toString()
                    isMajor = projectType.toFloat() == 1.0f
                }
            }
        }catch (e:Exception){
            LogUtils.e("PoiImport charge isMajorType error is $e")
        }finally {
            workbook = null
            inputStream?.close()
            inputStream = null
        }

        return isMajor
    }
}
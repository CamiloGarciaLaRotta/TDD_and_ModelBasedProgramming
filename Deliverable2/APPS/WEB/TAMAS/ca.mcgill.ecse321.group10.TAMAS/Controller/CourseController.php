<?php
require_once __DIR__.'\..\Controller\InputValidator.php';
require_once __DIR__.'\..\Persistence\PersistenceTAMAS.php';
require_once __DIR__.'\..\Model\CourseManager.php';
require_once __DIR__.'\..\Model\Course.php';

class CourseController{
	
	public function __construct(){
	}
	
	public function createCourse($class_name, $CDN, 
								$graderTimeBudget, $TATimeBudget) {
		//1. Validate input
		$name = InputValidator::validate_input($class_name);
		if($name==null || strlen($name) == 0){
			throw new Exception("Course name cannot be empty!");
		} else if(!is_numeric($CDN)) {
			throw new Exception("CDN must be a non null Integer!");
		} else if((!is_numeric($graderTimeBudget)) || (!is_numeric($TATimeBudget))) {
			throw new Exception("Time budget must be a non null Integer!");
		} else {
			//2. Load all of the data
			$pt = new PersistenceTAMAS();
			$cm = $pt->loadCourseManagerFromStore();
				
			//3. Add the new course
			$course = new Course($name, $CDN, $graderTimeBudget, $TATimeBudget);
			$cm->addCourse($course);
				
			//4. Write all the data
			$pt->writeCourseDataToStore($cm);
		}
	}

}
?>
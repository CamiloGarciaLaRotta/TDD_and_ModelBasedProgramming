package ca.mcgill.ecse321.group10.view;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.mcgill.ecse321.group10.TAMAS.model.ApplicationManager;
import ca.mcgill.ecse321.group10.TAMAS.model.Course;
import ca.mcgill.ecse321.group10.TAMAS.model.CourseManager;
import ca.mcgill.ecse321.group10.TAMAS.model.Instructor;
import ca.mcgill.ecse321.group10.TAMAS.model.Job;
import ca.mcgill.ecse321.group10.TAMAS.model.ProfileManager;
import ca.mcgill.ecse321.group10.controller.ApplicationController;
import ca.mcgill.ecse321.group10.controller.CourseController;
import ca.mcgill.ecse321.group10.controller.InputException;
import widgets.Constants;
import widgets.ThemedLabel;
import widgets.ThemedList;
import widgets.ThemedRadioButton;
import widgets.ThemedSpinner;
import widgets.ThemedTextField;

public class PublishJobView extends JFrame{

	private ApplicationManager am;
	private ProfileManager pm;
	private CourseManager cm;
	
	private JList instructorList;
	private JList courseList;
	private JScrollPane instructorScroller;
	private JScrollPane courseScroller;
	private JSpinner jDay;
	private JLabel lSalary;
	private JLabel lReqs;
	private JLabel errorLabel;
	private JLabel lPos;
	private JLabel lHours;
	private JLabel lRemaining;
	private JTextField tfSalary;
	private JTextField tfHours;
	private JTextField tfReqs;
	private JTextField tfRemaining;
	private JButton publish;
	private JRadioButton rbTA;
	private JRadioButton rbGrader;
	private ButtonGroup typeGroup;
	
	private DefaultListModel courseListModel;
	
	private String error;
	
	private Instructor instructor;
	
	public PublishJobView(ApplicationManager am, ProfileManager pm, CourseManager cm, Instructor instructor) {
		this.am = am;
		this.pm = pm;
		this.cm = cm;
		error = "";
		this.instructor = instructor;
		initComponents();
	}
	
	private void initComponents() {
		//setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Publish Job Posting");
		lSalary = new ThemedLabel("Salary: ");
		lReqs = new ThemedLabel("Requirements: ");
		tfSalary = new ThemedTextField();
		tfReqs = new ThemedTextField();
		errorLabel = new ThemedLabel("", ThemedLabel.LabelType.Error);
		publish = new JButton("Publish Job");
		
		lPos = new ThemedLabel("Position type: ");
		rbTA = new ThemedRadioButton("TA");
		rbTA.setSelected(true);
		rbGrader = new ThemedRadioButton("Grader");
		typeGroup = new ButtonGroup();
		typeGroup.add(rbTA);
		typeGroup.add(rbGrader);
		
		publish.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e) {
				publishPressed();
			}
		});
		String [] instructorNames = new String[pm.getInstructors().size()];
		for(int c = 0; c < instructorNames.length; c++) {
			instructorNames[c] = pm.getInstructor(c).getFirstName() + " " + pm.getInstructor(c).getLastName();
		}
		instructorList = new ThemedList(instructorNames);
		instructorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		instructorList.setLayoutOrientation(JList.VERTICAL);
		instructorScroller = new JScrollPane(instructorList);
		courseListModel = new DefaultListModel();
		courseList = new ThemedList(courseListModel);
		courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		courseList.setLayoutOrientation(JList.VERTICAL);
		courseScroller = new JScrollPane(courseList);
		
		if(instructor != null) {
			for(int c = 0; c < instructor.getCourses().size(); c++) {
				courseListModel.addElement(instructor.getCourse(c).getClassName());
			}
		}
		
		instructorList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				if(instructorList.getSelectedIndex() != -1) {
					courseListModel.clear();
					List<Course> courses = pm.getInstructor(instructorList.getSelectedIndex()).getCourses();
					for(int c = 0;c < courses.size(); c++) {
						courseListModel.addElement(courses.get(c).getClassName());
					}
					updateBudget();
				}
			}
		});
		
		courseList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) return;
				if(courseList.getSelectedIndex() != -1) {
					updateBudget();
				}
			}
		});
		
		rbTA.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				updateBudget();
			}
		});
		
		rbGrader.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				updateBudget();
			}
		});
		
		lHours = new ThemedLabel("Hours per semester:");
		tfHours = new ThemedTextField(15);
		lRemaining = new ThemedLabel("Remaining budget:");
		tfRemaining = new ThemedTextField(15);
		tfRemaining.setEnabled(false);
		
		String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
		jDay = new ThemedSpinner(new SpinnerListModel(days));
		
		
		GroupLayout layout = new GroupLayout(getContentPane());
	    getContentPane().setLayout(layout);
	    getContentPane().setBackground(Constants.bgColor);
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);
	    layout.setHorizontalGroup(
	    		layout.createParallelGroup()
	    		.addComponent(errorLabel)
	    		.addComponent(instructorScroller)
	    		.addComponent(courseScroller)
	    		.addGroup(
	    				layout.createSequentialGroup()
	    				.addComponent(lPos)
	    				.addComponent(rbTA)
	    				.addComponent(rbGrader)
	    				)
	    		.addGroup(
	    				layout.createSequentialGroup()
	    				.addComponent(lSalary)
	    				.addComponent(tfSalary,50,75,100)
	    				)
	    		.addGroup(
	    				layout.createSequentialGroup()
	    				.addComponent(lRemaining)
	    				.addComponent(tfRemaining,50,75,100)
	    				)
	    		.addGroup(
	    				layout.createSequentialGroup()
	    				.addComponent(lReqs)
	    				.addComponent(tfReqs, 50, 75, 100)
	    				)
	    		.addGroup(
	    				layout.createSequentialGroup()
	    				.addComponent(lHours)
	    				.addComponent(tfHours, 50, 75, 100)
	    				)
	    		.addComponent(jDay)
	    		.addComponent(publish)
	    		);
	    layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[]{lRemaining,lSalary,lReqs,lHours});
	    layout.setVerticalGroup(
	    		layout.createSequentialGroup()
	    		.addComponent(errorLabel)
	    		.addComponent(instructorScroller)
	    		.addComponent(courseScroller)
	    		.addGroup(
	    				layout.createParallelGroup()
	    				.addComponent(lPos)
	    				.addComponent(rbTA)
	    				.addComponent(rbGrader)
	    				)
	    		.addGroup(
	    				layout.createParallelGroup()
	    				.addComponent(lSalary)
	    				.addComponent(tfSalary,50,75,100)
	    				)
	    		.addGroup(
	    				layout.createParallelGroup()
	    				.addComponent(lRemaining)
	    				.addComponent(tfRemaining,50,75,100)
	    				)
	    		.addGroup(
	    				layout.createParallelGroup()
	    				.addComponent(lReqs)
	    				.addComponent(tfReqs, 50, 75, 100)
	    				)
	    		.addGroup(
	    				layout.createParallelGroup()
	    				.addComponent(lHours)
	    				.addComponent(tfHours, 50, 75, 100)
	    				)
	    		.addComponent(jDay)
	    		.addComponent(publish)
	    		);
	    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[]{lSalary,lReqs,tfSalary,tfReqs});
	    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[]{lHours,tfHours});
	    layout.linkSize(SwingConstants.VERTICAL, new java.awt.Component[]{lRemaining,tfRemaining});
	    
	    updateBudget();
	    
	    if(instructor != null) instructorScroller.setVisible(false);

	    pack();
	}
	
	private void refreshData() {
		errorLabel.setText(error);
		pack();
		if(error.length() != 0) return;
		tfSalary.setText("");
		tfReqs.setText("");
		jDay.setValue("Monday");
		rbGrader.setSelected(false);
		rbTA.setSelected(true);
		updateBudget();
		pack();
	}
	
	private void publishPressed() {
		error = "";
		if(instructor == null && instructorList.getSelectedIndex() == -1) error += "Instructor must be specified!\n";
		if(courseList.getSelectedIndex() == -1) error += "Course must be specified!\n";
		try {
			double salary = Double.parseDouble(tfSalary.getText());
			String requirements = tfReqs.getText();
			String day = (String)jDay.getValue();
			System.out.println("Day: " + day);
			if(instructor == null) instructor = pm.getInstructor(instructorList.getSelectedIndex());
			Course course = instructor.getCourse(courseList.getSelectedIndex());
			try {
				if(tfHours.getText().trim().length() == 0) throw new Exception("empty");
				float hours = Float.parseFloat(tfHours.getText());
				
				if(error.length() == 0) {
					ApplicationController ac = new ApplicationController(am,ApplicationController.APPLICATION_FILE_NAME);
					CourseController cc = new CourseController(cm,CourseController.COURSE_FILE_NAME);
					try {
						Job.Position pos = (rbTA.isSelected()) ? Job.Position.TA : Job.Position.GRADER;
						ac.addJobToSystem(hours, day, salary, requirements, course, instructor,pos);
						if(rbTA.isSelected()) cc.modifyTaBudget(course, hours * (float)salary);
						else cc.modifyGraderBudget(course, hours * (float)salary);
					} catch (InputException e) {
						error += e.getMessage();
					}
				}
			}catch(Exception e) {
				error += "Invalid entry for hours! Must be floating point number.";
			}
		}catch (Exception e) {
			error += "Salary must be a number!\n";
		}
		refreshData();
	}
	
	private void updateBudget() {
		if(courseList.getSelectedIndex() == -1) {
			tfRemaining.setText("");
			pack();
			return;
		}
		if(rbTA.isSelected()) tfRemaining.setText("$" + cm.getCourse(courseList.getSelectedIndex()).getTaBudget());
		else tfRemaining.setText("$" + cm.getCourse(courseList.getSelectedIndex()).getGraderBudget());
	}
	
}

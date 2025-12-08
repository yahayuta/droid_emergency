import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:geolocator/geolocator.dart';
import 'package:mailer/mailer.dart';
import 'package:mailer/smtp_server.dart';
import 'package:home_widget/home_widget.dart';
import 'package:flutter_background_service/flutter_background_service.dart';

class ConfigScreen extends StatefulWidget {
  const ConfigScreen({super.key});

  @override
  State<ConfigScreen> createState() => _ConfigScreenState();
}

class _ConfigScreenState extends State<ConfigScreen> {
  final _formKey = GlobalKey<FormState>();

  final _cusMsgController = TextEditingController();
  final _nameController = TextEditingController();
  final _gmailAddrController = TextEditingController();
  final _gmailPassController = TextEditingController();
  final _mail1Controller = TextEditingController();
  final _mail2Controller = TextEditingController();
  final _mail3Controller = TextEditingController();
  final _mail4Controller = TextEditingController();
  final _mail5Controller = TextEditingController();

  bool isServiceRunning = false;

  @override
  void initState() {
    super.initState();
    _loadData();
    HomeWidget.setAppGroupId('group.com.example.droid_emergency_flutter');
    HomeWidget.initiallyLaunchedFromHomeWidget().then(_launchedFromWidget);
    HomeWidget.widgetClicked.listen(_launchedFromWidget);

    final service = FlutterBackgroundService();
    service.on('running').listen((event) {
      if (mounted) {
        setState(() {
          isServiceRunning = event!['running'];
        });
      }
    });

    service.isRunning().then((value) {
      if (mounted) {
        setState(() {
          isServiceRunning = value;
        });
      }
    });
  }

  void _launchedFromWidget(Uri? uri) {
    if (uri?.toString() == 'home_widget:/emergency_button_clicked') {
      _sendMail();
    }
  }

  Future<void> _loadData() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _cusMsgController.text = prefs.getString('cusMsg') ?? '';
      _nameController.text = prefs.getString('name') ?? '';
      _gmailAddrController.text = prefs.getString('gmailAddr') ?? '';
      _gmailPassController.text = prefs.getString('gmailPass') ?? '';
      _mail1Controller.text = prefs.getString('mail1') ?? '';
      _mail2Controller.text = prefs.getString('mail2') ?? '';
      _mail3Controller.text = prefs.getString('mail3') ?? '';
      _mail4Controller.text = prefs.getString('mail4') ?? '';
      _mail5Controller.text = prefs.getString('mail5') ?? '';
    });
  }

  Future<void> _saveData() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('cusMsg', _cusMsgController.text);
    await prefs.setString('name', _nameController.text);
    await prefs.setString('gmailAddr', _gmailAddrController.text);
    await prefs.setString('gmailPass', _gmailPassController.text);
    await prefs.setString('mail1', _mail1Controller.text);
    await prefs.setString('mail2', _mail2Controller.text);
    await prefs.setString('mail3', _mail3Controller.text);
    await prefs.setString('mail4', _mail4Controller.text);
    await prefs.setString('mail5', _mail5Controller.text);
  }

  Future<Position?> _determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return null;
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return null;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return null;
    }

    return await Geolocator.getCurrentPosition();
  }

  Future<void> _sendMail() async {
    final position = await _determinePosition();
    final locationText = position != null
        ? 'https://www.google.com/maps?q=${position.latitude},${position.longitude}'
        : 'NO GPS DATA';

    final smtpServer = gmail(_gmailAddrController.text, _gmailPassController.text);
    final message = Message()
      ..from = Address(_gmailAddrController.text, _nameController.text)
      ..recipients.addAll([
        _mail1Controller.text,
        _mail2Controller.text,
        _mail3Controller.text,
        _mail4Controller.text,
        _mail5Controller.text,
      ].where((e) => e.isNotEmpty))
      ..subject = '${_nameController.text} NEEDS HELP!'
      ..text = '${_cusMsgController.text}\n\n$locationText';

    try {
      final sendReport = await send(message, smtpServer);
      print('Message sent: ' + sendReport.toString());
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('MAIL IS SENT')),
      );
    } on MailerException catch (e) {
      print('Message not sent. \n' + e.toString());
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('MAIL TRANSPORT ERROR!!')),
      );
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('INPUT YOUR INFORMATION'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              const Text(
                'YOUR INFORMATION',
                style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _cusMsgController,
                decoration: const InputDecoration(
                  labelText: 'CUSTOM MESSAGE(OPTIONAL)',
                ),
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _nameController,
                decoration: const InputDecoration(
                  labelText: 'YOUR NAME(REQUIRED)',
                ),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'YOUR NAME IS BLANK';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _gmailAddrController,
                decoration: const InputDecoration(
                  labelText: 'YOUR GMAIL ADDRESS (GMAIL ACCOUNT IS REQUIRED FOR THIS APPLICATION)',
                ),
                keyboardType: TextInputType.emailAddress,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'YOUR GMAIL ADDRES IS BLANK';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _gmailPassController,
                decoration: const InputDecoration(
                  labelText: 'YOUR GMAIL PASSWORD (GMAIL ACCOUNT IS REQUIRED FOR THIS APPLICATION)',
                ),
                obscureText: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'YOUR GMAIL PASSWORD IS BLANK';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _mail1Controller,
                decoration: const InputDecoration(
                  labelText: 'MAIL ADDRESS 1(REQUIRED)',
                ),
                keyboardType: TextInputType.emailAddress,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'MAIL ADDRESS 1 IS BLANK';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _mail2Controller,
                decoration: const InputDecoration(
                  labelText: 'MAIL ADDRESS 2(OPTIONAL)',
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _mail3Controller,
                decoration: const InputDecoration(
                  labelText: 'MAIL ADDRESS 3(OPTIONAL)',
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _mail4Controller,
                decoration: const InputDecoration(
                  labelText: 'MAIL ADDRESS 4(OPTIONAL)',
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16),
              TextFormField(
                controller: _mail5Controller,
                decoration: const InputDecoration(
                  labelText: 'MAIL ADDRESS 5(OPTIONAL)',
                ),
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 32),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        if (_formKey.currentState!.validate()) {
                          _saveData();
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('YOUR INFORMATION IS SAVED!')),
                          );
                        }
                      },
                      child: const Text('SAVE'),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        if (_formKey.currentState!.validate()) {
                          _sendMail();
                        }
                      },
                      child: const Text('SEND TEST MAIL'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        FlutterBackgroundService().startService();
                      },
                      child: const Text('Start Service'),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        FlutterBackgroundService().invoke("stopService");
                      },
                      child: const Text('Stop Service'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Text(isServiceRunning ? 'Service is running' : 'Service is stopped'),
            ],
          ),
        ),
      ),
    );
  }
}
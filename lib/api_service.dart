import 'package:amplify_auth_cognito/amplify_auth_cognito.dart';
import 'package:amplify_flutter/amplify_flutter.dart';

import 'package:http/http.dart' as http;

class ApiService {
  late JsonWebToken _bearer;
  bool _tokensRetrieved = false;

  Future<JsonWebToken> _getBearer() async {
    if (!_tokensRetrieved) {
      final cognitoPlugin =
          Amplify.Auth.getPlugin(AmplifyAuthCognito.pluginKey);
      final result = await cognitoPlugin.fetchAuthSession();
      final currentUser = await cognitoPlugin.getCurrentUser();
      print(currentUser);
      var tokens = result.userPoolTokensResult.value;
      _bearer = tokens.accessToken;
      _bearer = tokens.idToken;
      var familyName = _bearer.familyName;
      _tokensRetrieved = true;
    }
    return Future<JsonWebToken>(() => _bearer);
  }

  makeApiCall() async {
    final bearer = await _getBearer();
    print(bearer);
    var response = await http.get(Uri.parse("http://localhost:8080/api/test"),
        headers: {"Authorization": "Bearer ${bearer.raw}"});
    print(response.body);
  }
}
